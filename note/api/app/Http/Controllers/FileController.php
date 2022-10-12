<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use App\Http\Controllers\MailController;
use Illuminate\Http\Response;
use App\Model\FileApp;
use App\Model\RequestException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class FileController extends Controller

{

    use AuthenticatesUsers;

	public function __construct(){
      $this->middleware('basicAuth');
   }





   /**
    * request fields
    * - SessionID
    * - UserID
    * - File (Stream)
    * - FileApp (Object send as json)
    * must parse to array 
    * 
    */

    public function fileSave(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $fileName = $BodyData->FileApp->FileAppID.".".$BodyData->FileApp->FileExtension;
         $FileContent = $request->file('fileUpload');

         $pathSave = Storage::putFileAs('', $FileContent, $fileName);
         //$BodyData->FileApp->Path = Storage::url($pathSave);
         if(strlen($pathSave) > 0){
            $BodyData->FileApp->Path = $pathSave;

            $fileObj = FileApp::updateOrCreate( [['FileAppID',$BodyData->FileApp->FileAppID], ['CreatedBy', $BodyData->UserID] ] , (array)$BodyData->FileApp);
            return parent::responseDataSuccess(['FileApp'=>$fileObj]);     
         }else{
            return parent::responseCode(405);
         }
      }
      return parent::responseCode(400);
   }

   /**
    * request fields
    * - SessionID
    * - UserID
    * 
    */
   public function fileGetAll(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $listFile = FileApp::where([['CreatedBy', $BodyData->UserID ], ['Deleted', false]])->get();
         return parent::responseDataSuccess(['ListFileApp'=>$listFile]);   
      }
      return parent::responseCode(400);
   }

   /**
    * request fields
    * - SessionID
    * - UserID
    * - Limit
    * - Offset
    */
    public function fileGetList(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $listFile = FileApp::where([['CreatedBy', $BodyData->UserID ], ['Deleted', false]])->limit($BodyData->Limit)->offset($BodyData->Offset)->get();
         return parent::responseDataSuccess(['Limit'=>$BodyData->Limit, 'Offset' => $BodyData->Offset, 'ListFileApp'=>$listFile]);   
      }
      return parent::responseCode(400);
   }

   /**
    * request fields
    * - SessionID
    * - UserID
    * - IDs
    */
   public function fileDelete(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
            FileApp::whereIn('FileAppID', $BodyData->IDs)
            ->where([['CreatedBy', $BodyData->UserID],['Deleted', 0]])
            ->update(['Deleted'=> 1, 'ModifiedDate' => $BodyData->Time]);;
            return parent::responseCode(200);// methob not allow
         }
      }
      return parent::responseCode(400);// bad request
   }
}

