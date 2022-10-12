<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use Illuminate\Http\Response;
use App\Model\Session;
use App\Model\Note;
use App\Model\NoteFile;
use Illuminate\Http\Request;

class NoteController extends Controller
{

    use AuthenticatesUsers;

	public function __construct()
   {
      //$this->middleware('auth:api');
      $this->middleware('basicAuth');
   }


   /**
    * request fields
    * - SessionID
    * - UserID
    * - Note (Object)
    * 
    */
   public function noteSave(Request $request){
      $BodyData = $request->BodyData;
      if(parent::isLogged($BodyData) !=null){
         try {
            $noteObj = Note::updateOrCreate( [['NoteID',$BodyData->Note->NoteID], ['UserID', $BodyData->UserID] ] ,(array)$BodyData->Note);
            if($noteObj != null){
               $noteFileList = (array)$BodyData->NoteFiles;
               $listLength = count($noteFileList);
               for($i = 0; $i< $listLength; $i++){
                  NoteFile::updateOrCreate( [['NoteFileID',$noteFileList[$i]->NoteFileID], ['CreatedBy', $BodyData->UserID] ] ,(array)$noteFileList[$i]);
               }
               return parent::responseCode(200);   
            }
         } catch (\Exception $e) {
            //return parent::responseData( ['Note' => $NoteObj, 'NoteFiles'=>$NoteFileArr] , 304);   
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
    public function noteDelete(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
            Note::whereIn('NoteID', $BodyData->IDs)
            ->where([['UserID', $BodyData->UserID],['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);

            NoteFile::whereIn('NoteID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
            return parent::responseCode(200);// methob not allow
         }
      }
      return parent::responseCode(400);// bad request
   }

   /**
       * request fields
       * SessionID
       * UserID
       * IDs
       */
       public function noteDeleteFile(Request $request){
         $BodyData = $request->BodyData;
          if(sizeof($BodyData->IDs) > 0){
            if(parent::isLogged($BodyData) !=null){
               NoteFile::whereIn('NoteFileID', $BodyData->IDs)
               ->where([['Deleted', false]])
               ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
               return parent::responseCode(200);// methob not allow
            }
         }
         return parent::responseCode(400);// bad request
      }

   /**
    * request fields
    * SessionID
    * UserID
    * NoteID
    */
    public function noteGetDetail(Request $request){
      $BodyData = $request->BodyData;
      if(strlen($BodyData->NoteID) >0){
         if(parent::isLogged($BodyData) !=null){
            $NoteObj = Note::where([['UserID', $UserID], ['NoteID', $BodyData->NoteID]])->first();
            $NoteFileArr = NoteFile::where([ ['NoteID', $BodyData->NoteID]])->get();
            return parent::responseDataSuccess(['Note' => $NoteObj, 'NoteFiles'=>$NoteFileArr]);
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
    public function noteGetList(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $listNote = Note::where([['UserID', $BodyData->$UserID],[['Deleted', false]]])->limit($BodyData->Limit)->offset($BodyData->Offset)->get();
         return parent::responseDataSuccess(['Limit'=>$BodyData->Limit, 'Offset' => $BodyData->Offset, 'ListNote'=>$listNote]);   
      }
      return parent::responseCode(400);
   }

   /**
    * request fields
    * SessionID
    * UserID
    */
    public function noteGetAll(Request $request){
      $BodyData = $request->BodyData;
      
      if(parent::isLogged($BodyData) !=null){
         $listNote = Note::where([['UserID', $BodyData->UserID], ['Deleted', false]])->get();
         $NoteFileArr = NoteFile::where([ ['CreatedBy', $BodyData->UserID]])->get();
         return parent::responseDataSuccess(['ListNote'=>$listNote, 'NoteFiles'=>$NoteFileArr]);   
      }
      return parent::responseCode(400);
   }
   

}
