<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use Illuminate\Http\Response;
use App\Model\ErrorTracking;
use App\Model\Option;
use Illuminate\Http\Request;

class CommonController extends Controller
{

    use AuthenticatesUsers;

	public function __construct()
   {
      //$this->middleware('auth:api');
      $this->middleware('basicAuth');
   }

   

//////////////////////////////////////////////////////////////////////////////////////////////////////////
///// support function on top
//////////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * request fields
    * - SessionID
    * - UserID
    * - ErrorTracking (Object)
    * 
    */
    public function sendErrors(Request $request){
      $BodyData = $request->BodyData;
      if(parent::isLogged($BodyData) !=null){
         $errorList = (array)$BodyData->Errors;
         $listLength = count($errorList);
         for($i = 0; $i< $listLength; $i++){
            ErrorTracking::updateOrCreate( [['ErrorID',$errorList[$i]->ErrorID], ['CreatedBy', $BodyData->UserID] ] ,(array)$errorList[$i]);
         }
         return parent::responseCode(200);   
      }
      return parent::responseCode(400);// bad request
   }

   public function getServerConfig(Request $request){
	   $BodyData = $request->BodyData;
	   $listOption = null;
      if(parent::isLogged($BodyData) !=null){
         $listOption = Option::where([['UserID', $BodyData->$UserID]])->get();
      }
      return parent::responseDataSuccess(['CgfUrlPathFile'=>'/public/storage/', 'CgfCardService'=>true, 'ListOption'=>$listOption]);
   }



   /**
    * request fields
    * - SessionID
    * - UserID
    * - Option (Object)
    * 
    */
   public function optionSave(Request $request){
      $BodyData = $request->BodyData;
      if(parent::isLogged($BodyData) !=null){
         try {
            $optionObj = Option::updateOrCreate( [['OptionID',$BodyData->Option->OptionID], ['UserID', $BodyData->UserID] ] ,(array)$BodyData->Option);
            if($optionObj != null){
               return parent::responseCode(200);   
            }
         } catch (\Exception $e) {
            return parent::responseCode(304); // not modified
         }
      }
      return parent::responseCode(400);
   }

   /**
    * request fields
    * SessionID
    * UserID
    * IDs
    */
    public function optionDelete(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
            Option::whereIn('OptionID', $BodyData->IDs)
            ->where([['UserID', $BodyData->UserID],['Deleted', ]])
            ->update(['Deleted'=> true]);
            return parent::responseCode(200);
         }
      }
      return parent::responseCode(400);// bad request
   }
   
   /**
    * request fields
    * SessionID
    * UserID
    * Limit
    * Offset
    */
    public function optionGetAll(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $listOption = Option::where([['UserID', $BodyData->$UserID],[['Deleted', false]]])->limit($BodyData->Limit)->offset($BodyData->Offset)->get();
         return parent::responseDataSuccess(['Limit'=>$BodyData->Limit, 'Offset' => $BodyData->Offset, 'ListOption'=>$listOption]);   
      }
      return parent::responseCode(400);
   }



}
