<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use Illuminate\Http\Response;
use App\Model\Session;
use App\Model\Bill;
use App\Model\BillItem;
use App\Model\BillFile;
use Illuminate\Http\Request;

class BillController extends Controller
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
    * - Bill (Object)
	* - BillItems (list Object)
	* - BillFiles (list Object)
    *
    */
   public function billSave(Request $request){
      $BodyData = $request->BodyData;
      if(parent::isLogged($BodyData) !=null){
         try {
            $billObj = Bill::updateOrCreate( [['BillID',$BodyData->Bill->BillID], ['UserID', $BodyData->UserID] ] ,(array)$BodyData->Bill);
            if($billObj != null){
               $billItemList = (array)$BodyData->BillItems;
               $listLength = count($billItemList);
               for($i = 0; $i< $listLength; $i++){
                  BillItem::updateOrCreate( [['BillItemID',$billItemList[$i]->BillItemID], ['CreatedBy', $BodyData->UserID] ] ,(array)$billItemList[$i]);
               }

               $billFileList = (array)$BodyData->BillFiles;
               $listFileLength = count($billFileList);
               for($i = 0; $i< $listFileLength; $i++){
                  BillFile::updateOrCreate( [['BillFileID',$billFileList[$i]->BillFileID], ['CreatedBy', $BodyData->UserID] ] ,(array)$billFileList[$i]);
               }
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
    public function billDelete(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
			BillItem::whereIn('BillID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
			
			BillFile::whereIn('BillID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
			
            Bill::whereIn('BillID', $BodyData->IDs)
            ->where([['UserID', $BodyData->UserID],['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
            return parent::responseCode(200);
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
    public function billDeleteFile(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
			BillFile::whereIn('BillFileID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
            return parent::responseCode(200);
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
    public function billDeleteItem(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
			BillItem::whereIn('BillItemID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
            return parent::responseCode(200);
         }
      }
      return parent::responseCode(400);// bad request
   }
   /**
    * request fields
    * SessionID
    * UserID
    * id
    */
    public function billGetDetail(Request $request){
      $BodyData = $request->BodyData;
      if(strlen($BodyData->id) >0){
         if(parent::isLogged($BodyData) !=null){
            $billObj = Bill::where([['UserID', $UserID], ['BillID', $BodyData->id]])->first();
            $billItemList = BillItem::where([ ['BillID', $BodyData->id], ['Deleted', false]])->get();
            $billFileList = BillFile::where([ ['BillID', $BodyData->id], ['Deleted', false]])->get();
            return parent::responseDataSuccess(['Bill' => $billObj, 'BillItems'=>$billItemList, 'BillFiles'=>$billFileList]);
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
    public function billGetList(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $listBill = Bill::where([['UserID', $BodyData->$UserID],[['Deleted', false]]])->limit($BodyData->Limit)->offset($BodyData->Offset)->get();
         return parent::responseDataSuccess(['Limit'=>$BodyData->Limit, 'Offset' => $BodyData->Offset, 'ListBill'=>$listBill]);   
      }
      return parent::responseCode(400);
   }

   /**
    * request fields
    * SessionID
    * UserID
    */
    public function billGetAll(Request $request){
      $BodyData = $request->BodyData;
      
      if(parent::isLogged($BodyData) !=null){
         $listBill = Bill::where([['UserID', $BodyData->UserID], ['Deleted', false]])->get();
         $billItemList = BillItem::where([ ['CreatedBy', $BodyData->UserID], ['Deleted', false]])->get();
         $billFileList = BillFile::where([ ['CreatedBy', $BodyData->UserID], ['Deleted', false]])->get();
         return parent::responseDataSuccess(['ListBill'=>$listBill, 'BillItems'=>$billItemList, 'BillFiles'=>$billFileList]);   
      }
      return parent::responseCode(400);
   }
   

}
