<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use App\Http\Controllers\MailController;
use Illuminate\Http\Response;
use App\Model\Password;
use App\Model\UserApp;
use App\Model\Session;
use App\Model\Profile;
use App\Model\Avatar;
use App\Model\RequestException;
use Illuminate\Http\Request;

class UserController extends Controller
{

    use AuthenticatesUsers;

	public function __construct()
   {
      //$this->middleware('auth:api');
      $this->middleware('basicAuth');
   }

   /**
    * check exist code
    * return object if exist or null
    */
   public function validCode($userID, $requestType, $code ){
      // check valid code
      $exceptionObj = RequestException::where([['UserID', $userID],
          ['RequestType', $requestType],
          ['RequestCode', $code],
          ['IsActive', false]])->first();
      return $exceptionObj;
  }

  private function getUser($userName, $email){
   if(strlen($userName) >0 && strlen($userName)<= parent::LARGE_LENGTH){ // exist user name
      $user = UserApp::where('UserName', $userName)->first();
      return $user;
   }else if(strlen($email) >0 && strlen($email)<= parent::LARGE_LENGTH){ // exist email
      $user = UserApp::where('EmailAddress', $email)->first();
      return $user;
   }else{
      return null;
   }
  }

   /**
    * check exist user
    * return user ID if exist or null
    */
   private function checkExistUser($userName, $email){
      $user = self::getUser($userName, $email);
      return $user['UserID'];
   }

   /**
    * check password
    * return true / false
    * $passEncryption = Hash::make("Password"); 
    * $isRight = Hash::check("Password" , $passEncryption) == 1;
    */
    private function checkPassword($userID, $password){
      if(strlen($userID) >0 && strlen($userID)<= parent::LARGE_LENGTH
         && strlen($password) >0 && strlen($password)<= parent::MEDIUM_LENGTH){
         $passObj = Password::where([['UserID', $userID], ['IsActive', true]])->first();
         return Hash::check($password, $passObj['PasswordContent']) == 1;
      }else{
         return false;
      }
   }
   
   /** 
    * check user info for login or create new user
    * return userID
    */
    private function checkLogin($userName, $email, $password, $time){
       $userID = self::checkExistUser($userName, $email);
       if($userID != null && strlen($userID) >0){// exist user => check pass
         if(self::checkPassword($userID, $password)){
            return $userID;
         }else{
            return null;
         } 
       }else{ // not exist user => create
         $userObj = UserApp::create(['UserID' => parent::generateID(),
            'UserName' => $userName,
            'EmailAddress' => $email,
            'CreatedBy' => '',
            'CreatedDate' => $time]);

         $passObj = Password::create(['PasswordID' => parent::generateID(),
            'PasswordContent' => Hash::make($password),
            'UserID' => $userObj['UserID'],
            'CreatedBy' => $userObj['UserID'],
            'CreatedDate' => $time]);
         
         $profileObj = Profile::create([ 'ProfileID' => parent::generateID(), 
            'UserID' => $userObj['UserID'], 
            'CreatedBy' => $userObj['UserID'],
            'CreatedDate' => $time]);

         return $userObj['UserID'];
       }
   }

//////////////////////////////////////////////////////////////////////////////////////////////////////////
///// support function on top
//////////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * when the user is not logged in
    * we take user to Login / register
    * return user info + session\
    * Request input:
    * - UserName
    * - EmailAddress (request one of UserName or EmailAddress)
    * - PasswordContent
    * - DeviceID
    * - SessionType
    * - Time
    */
   public function registerSession(Request $request){
      $BodyData = $request->BodyData;
      if((strlen($BodyData->UserName) >0 && strlen($BodyData->UserName)<= parent::LARGE_LENGTH
            || strlen($BodyData->EmailAddress) >0 && strlen($BodyData->EmailAddress)<= parent::LARGE_LENGTH)
         && strlen($BodyData->PasswordContent) >0 && strlen($BodyData->PasswordContent)<= parent::MEDIUM_LENGTH
         && strlen($BodyData->Time) >0){
         // check exist user
         // | not => register
         // | yes => login
         $userID = self::checkLogin($BodyData->UserName, $BodyData->EmailAddress, $BodyData->PasswordContent, $BodyData->Time);
         if($userID == null || strlen($userID) <=0){
            return parent::responseCode(401);
         }else{
            //make session
            // close all current session
            Session::where([['UserID', $userID ],['SessionType', $BodyData->SessionType ], ['IsActive', true]])
               ->update(['IsActive'=> false, 'EndTime' => $BodyData->Time]);
            // make the new session
            $session = Session::create(['SessionID' => parent::generateID() ,
               'UserID' => $userID,
               'SessionType' => $BodyData->SessionType, 
               'DeviceID' =>  $BodyData->DeviceID, 
               'StartTime' => $BodyData->Time, 
               'BuildType' => $BodyData->BuildType, 
               'EndTime' => 0,
               'IsActive' => true, 
               'IpAddress' => $request->ip()]);

            $profile = Profile::where('UserID', $userID)->first();
            return parent::responseDataSuccess(['SessionID'=>$session['SessionID'], 'Profile'=>$profile]);
         }
      }
      return parent::responseCode(400);// bad request
   }


   /**
    * when user get code
    * flow: 
    * step 1: request code
    * step 2: system send code to mail
    * step 3: user input code
    * Request input:
    * - Email
    * - RequestType
    * 
    */
   public function getCode(Request $request){
      // check email address existed 
      $BodyData = $request->BodyData;
      $userID = self::checkExistUser('', $BodyData->Email);
      if($userID != null && strlen($userID) >0){// exist user
         // make reset code and send to mail
         $specialCode = parent::generateCode();
         // insert to database
         $exceptionObj = RequestException::updateOrCreate(
            [['UserID', $userID], ['RequestType', $BodyData->RequestType], ['IsActive', false]],
            ['RequestExceptionID' => parent::generateID() ,
            'RequestType' => $BodyData->RequestType,
            'RequestCode' => $specialCode, 
            'IsActive' =>  false, 
            'TimeStart' => $BodyData->Time, 
            'TimeEnd' => 0,
            'Tag' => $BodyData->Email,
            'UserID' => $userID]);
         // send mail
         if($exceptionObj != null){
            $mCtrl = new MailController();
            $mCtrl->sendMail($BodyData->RequestType, $BodyData->Email , $specialCode );
            // return response ok
            return parent::responseCode(200); 
         }
       }
      // by any exception => return error
      return parent::responseCode(400); 
   }


   /**
    * Request input:
    * - Email
    * - Code
    * - PasswordContent
    * - RequestType
    * - Time
    */
   public function resetPassword(Request $request){
      // check email address existed 
      $BodyData = $request->BodyData;
      $userID = self::checkExistUser('', $BodyData->Email);
      if($userID != null 
         && strlen($userID) >0
         && strlen($BodyData->PasswordContent) > 0 && strlen($BodyData->PasswordContent) <= parent::MEDIUM_LENGTH){// exist user
         // check valid code
         $exceptionObj = self::validCode($userID, $BodyData->RequestType , $BodyData->Code );
         if($exceptionObj != null && strlen($exceptionObj['RequestExceptionID']) > 0){
            // dismiss code
            RequestException::where( 'RequestExceptionID', $exceptionObj['RequestExceptionID'])
               ->update(['IsActive'=> true, 'TimeEnd' => $BodyData->Time]);
            // close all current password
            Password::where([['UserID', $userID ], ['IsActive', true]])
               ->update(['IsActive'=> false]);
            // insert new password
            $passObj = Password::create(['PasswordID' => parent::generateID(),
               'PasswordContent' => Hash::make($BodyData->PasswordContent),
               'UserID' => $userID,
               'CreatedBy' => $userID,
               'CreatedDate' => $BodyData->Time]);
            return parent::responseCode(200); 
         }
       }
      // by any exception => return error
      return parent::responseCode(400); 
   }

   /**
    * Request input:
    * - UserID
    * - SessionID
    * - SessionType
    * - Time
    */
   public function logOut(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         // close all current session
         Session::where([['UserID', $BodyData->UserID ], ['IsActive', true], ['SessionType',$BodyData->SessionType]])
         ->update(['IsActive'=> false, 'EndTime' => $BodyData->Time]);
      }
      return parent::responseCode(200);// allway allow logOut
   }

   /**
    * Request input:
    * - UserID
    * - SessionID
    * - Profile
    * - Avatar
    */
   public function updateProfile(Request $request){
      $BodyData = $request->BodyData;
       
      if(parent::isLogged($BodyData) !=null){
         try{
            $profileObj = Profile::updateOrCreate( [['ProfileID',$BodyData->Profile->ProfileID], ['UserID', $BodyData->UserID]] , (array)$BodyData->Profile);
            if(property_exists($BodyData , 'Avatar')){
               // close all current avatart
               Avatar::where([['UserID', $BodyData->UserID ], ['IsActive', true]])
               ->update(['IsActive'=> false]);
               // insert new password
               Avatar::updateOrCreate( [['AvatarID',$BodyData->Avatar->AvatarID], ['UserID', $BodyData->UserID]] , (array)$BodyData->Avatar);
            }
            return parent::responseDataSuccess(['Profile'=>$profileObj]);   
         } catch (\Exception $e) {
            return parent::responseCode(304); // not modified
         }
      }
      return parent::responseCode(400);// methob not allow
   }

   /**
    * Request input:
    * - UserID
    * - SessionID
    * - RequestType
    * - Account
    * - Code
    * - Email
    */
    public function changeAccount(Request $request){
      $BodyData = $request->BodyData;

      if(strlen($BodyData->Account) >0 && strlen($BodyData->Account) <= parent::LARGE_LENGTH){
         if(parent::isLogged($BodyData) !=null){
            $exceptionObj = self::validCode($BodyData->UserID, $BodyData->RequestType, $BodyData->Code);
            if($exceptionObj != null && strlen($exceptionObj['RequestExceptionID']) > 0){
               $userObj = self::getUser('', $BodyData->Email);
               if($userObj != null && strlen($userObj["UserID"]) > 0 && $userObj["UserID"] == $BodyData->UserID){
                  // dismiss code
                  RequestException::where( 'RequestExceptionID', $exceptionObj['RequestExceptionID'])
                     ->update(['IsActive'=> true, 'TimeEnd' => $BodyData->Time]);
                  // update user name
                  UserApp::where([['UserID', $BodyData->UserID ]])
                     ->update(['UserName'=> $BodyData->Account]);
                  return parent::responseCode(200); 
               }
            }
         }
      }
      // by any exception => return error
      return parent::responseCode(400); 
   }

   /**
    * 2 cases: don't existed mail before and existed mail
    ******************
    * case 1: don't existed mail
    * check exist for new mail
    * check exist account => update mail
    * Request input:
    * - UserID
    * - SessionID
    * - Account
    * - NewEmail
    ******************
    * case 2: existed mail
    * check exist for new mail
    * check exist for current mail => update mail
    * Request input:
    * - UserID
    * - SessionID
    * - Email
    * - NewEmail
    * - Code
    */
    public function changeMail(Request $request){
      $BodyData = $request->BodyData;

      if(strlen($BodyData->NewEmail) >0 && strlen($BodyData->NewEmail) <= parent::LARGE_LENGTH){
         if(parent::isLogged($BodyData) !=null){
            // check NewEmail existed
            $userExisted = self::getUser('', $BodyData->NewEmail);
            if($userExisted == null){
               // case 1: don't existed mail
               if(strlen($BodyData->Code) <= 0 ){
                  $userObj = self::getUser($BodyData->Account, '');
                  if($userObj != null && strlen($userObj["UserID"]) > 0 && strlen($userObj["EmailAddress"]) ==0 && $userObj["UserID"] == $BodyData->UserID){
                     // update email
                     UserApp::where([['UserID', $BodyData->UserID ]])
                     ->update(['EmailAddress'=> $BodyData->NewEmail]);
                     return parent::responseCode(200);
                  }
               }else{//case 2: existed mail
                  $exceptionObj = self::validCode($BodyData->UserID, $BodyData->RequestType, $BodyData->Code);
                  if($exceptionObj != null && strlen($exceptionObj['RequestExceptionID']) > 0){
                     $userObj = self::getUser('', $BodyData->Email);
                     if($userObj != null && strlen($userObj["UserID"]) > 0 && $userObj["UserID"] == $BodyData->UserID){
                        // dismiss code
                        RequestException::where( 'RequestExceptionID', $exceptionObj['RequestExceptionID'])
                        ->update(['IsActive'=> true, 'TimeEnd' => $BodyData->Time]);
                        // update email
                        UserApp::where([['UserID', $BodyData->UserID ]])
                        ->update(['EmailAddress'=> $BodyData->NewEmail]);
                        return parent::responseCode(200);
                     }
                  }
               }
            }
         }
      }
      // by any exception => return error
      return parent::responseCode(400); 
   }


   /**
    * Request input:
    * - UserID
    * - SessionID
    * - PasswordContent
    * - OldPass
    */
    public function changePassword(Request $request){
      $BodyData = $request->BodyData;
       
      if(strlen($BodyData->PasswordContent) > 0 && strlen($BodyData->PasswordContent) <= parent::MEDIUM_LENGTH){
            if(parent::isLogged($BodyData) !=null){
               if(self::checkPassword($BodyData->UserID, $BodyData->OldPass)){
                  // close all current password
                  Password::where([['UserID', $BodyData->UserID ], ['IsActive', true]])
                  ->update(['IsActive'=> false]);
                  // insert new password
                  $passObj = Password::create(['PasswordID' => parent::generateID(),
                     'PasswordContent' => Hash::make($BodyData->PasswordContent),
                     'UserID' => $BodyData->UserID,
                     'CreatedBy' => $BodyData->UserID,
                     'CreatedDate' => $BodyData->Time]);
                  return parent::responseCode(200);
               }
            }
      }
      return parent::responseCode(400);// bad request
   }


}
