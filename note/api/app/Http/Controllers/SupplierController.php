<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use Illuminate\Http\Response;
use App\Model\Session;
use App\Model\Supplier;
use App\Model\SupplierContaction;
use App\Model\SupplierFile;
use Illuminate\Http\Request;

class SupplierController extends Controller
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
    * - Supplier (Object)
	* - SupplierFiles (list Object)
	* - SupplierContactions (list Object)
    *
    */
   public function supplierSave(Request $request){
      $BodyData = $request->BodyData;
      if(parent::isLogged($BodyData) !=null){
         try {
            $supplierObj = Supplier::updateOrCreate( [['SupplierID',$BodyData->Supplier->SupplierID], ['CreatedBy', $BodyData->UserID] ] ,(array)$BodyData->Supplier);
            if($supplierObj != null){

               $supplierFileList = (array)$BodyData->SupplierFiles;
               $listFileLength = count($supplierFileList);
               for($i = 0; $i< $listFileLength; $i++){
                  SupplierFile::updateOrCreate( [['SupplierFileID',$supplierFileList[$i]->SupplierFileID], ['CreatedBy', $BodyData->UserID] ] ,(array)$supplierFileList[$i]);
               }

               $supplierContactList = (array)$BodyData->SupplierContactions;
               $listContactLength = count($supplierContactList);
               for($i = 0; $i< $listContactLength; $i++){
                  SupplierContaction::updateOrCreate( [['SupplierContactionID',$supplierContactList[$i]->SupplierContactionID], ['CreatedBy', $BodyData->UserID] ] ,(array)$supplierContactList[$i]);
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
    public function supplierDelete(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
			Supplier::whereIn('SupplierID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
			
			SupplierFile::whereIn('SupplierID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
			
            SupplierContaction::whereIn('SupplierID', $BodyData->IDs)
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
  * IDs of SupplierContact ID
  */
   public function supplierDeleteContact(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){

            SupplierContaction::whereIn('SupplierContactionID', $BodyData->IDs)
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
   * IDs of supplierFile ID
   */
   public function supplierDeleteFile(Request $request){
         $BodyData = $request->BodyData;
          if(sizeof($BodyData->IDs) > 0){
            if(parent::isLogged($BodyData) !=null){
   			SupplierFile::whereIn('SupplierFileID', $BodyData->IDs)
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
    public function supplierGetDetail(Request $request){
      $BodyData = $request->BodyData;
      if(strlen($BodyData->id) >0){
         if(parent::isLogged($BodyData) !=null){
            $supplierObj = Supplier::where([['SupplierID', $BodyData->id]])->first();
            $supplierContactionList = SupplierContaction::where([ ['SupplierID', $BodyData->id], ['Deleted', false]])->get();
            $supplierFileList = SupplierFile::where([ ['SupplierID', $BodyData->id], ['Deleted', false]])->get();
            return parent::responseDataSuccess(['Supplier' => $supplierObj, 'SupplierContactions'=>$supplierContactionList, 'SupplierFiles'=>$supplierFileList]);
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
    public function supplierGetList(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $listSupplier = Supplier::where([['CreatedBy', $BodyData->$UserID],[['Deleted', false]]])->limit($BodyData->Limit)->offset($BodyData->Offset)->get();
         return parent::responseDataSuccess(['Limit'=>$BodyData->Limit, 'Offset' => $BodyData->Offset, 'ListSupplier'=>$listSupplier]);
      }
      return parent::responseCode(400);
   }

   /**
    * request fields
    * SessionID
    * UserID
    */
    public function supplierGetAll(Request $request){
      $BodyData = $request->BodyData;
      
      if(parent::isLogged($BodyData) !=null){
         $listSupplier = Supplier::where([['CreatedBy', $BodyData->UserID], ['Deleted', false]])->get();
         $listSupplierFile = SupplierFile::where([ ['CreatedBy', $BodyData->UserID], ['Deleted', false]])->get();
         $listSupplierContact = SupplierContaction::where([ ['CreatedBy', $BodyData->UserID], ['Deleted', false]])->get();
         return parent::responseDataSuccess(['ListSupplier'=>$listSupplier, 'SupplierFiles'=>$listSupplierFile, 'SupplierContactions'=>$listSupplierContact]);
      }
      return parent::responseCode(400);
   }
   

}
