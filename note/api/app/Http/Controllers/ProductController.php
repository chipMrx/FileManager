<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use Illuminate\Http\Response;
use App\Model\Session;
use App\Model\Product;
use App\Model\ProductPrice;
use App\Model\ProductFile;
use Illuminate\Http\Request;

class ProductController extends Controller
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
    * - Product (Object)
	* - ProductFiles (list Object)
	* - ProductPrices (list Object)
    *
    */
   public function productSave(Request $request){
      $BodyData = $request->BodyData;
      if(parent::isLogged($BodyData) !=null){
         try {
            $productObj = Product::updateOrCreate( [['ProductID',$BodyData->Product->ProductID], ['CreatedBy', $BodyData->UserID] ] ,(array)$BodyData->Product);
            if($supplierObj != null){

               $productFileList = (array)$BodyData->ProductFiles;
               $listFileLength = count($productFileList);
               for($i = 0; $i< $listFileLength; $i++){
                  ProductFile::updateOrCreate( [['ProductFileID',$productFileList[$i]->ProductFileID], ['CreatedBy', $BodyData->UserID] ] ,(array)$productFileList[$i]);
               }

               $productPriceList = (array)$BodyData->ProductPrices;
               $listPriceLength = count($productPriceList);
               for($i = 0; $i< $listPriceLength; $i++){
                  ProductPrice::updateOrCreate( [['ProductPriceID',$productPriceList[$i]->ProductPriceID], ['CreatedBy', $BodyData->UserID] ] ,(array)$productPriceList[$i]);
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
    public function productDelete(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
			Product::whereIn('ProductID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
			
			ProductPrice::whereIn('ProductID', $BodyData->IDs)
            ->where([['Deleted', false]])
            ->update(['Deleted'=> true, 'ModifiedDate' => $BodyData->Time]);
			
            ProductFile::whereIn('ProductID', $BodyData->IDs)
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
  * IDs of ProductPrice ID
  */
   public function productDeletePrice(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){

            ProductPrice::whereIn('ProductPriceID', $BodyData->IDs)
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
   * IDs of productFile ID
   */
   public function productDeleteFile(Request $request){
         $BodyData = $request->BodyData;
          if(sizeof($BodyData->IDs) > 0){
            if(parent::isLogged($BodyData) !=null){
   			ProductFile::whereIn('ProductPriceID', $BodyData->IDs)
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
    public function productGetDetail(Request $request){
      $BodyData = $request->BodyData;
      if(strlen($BodyData->id) >0){
         if(parent::isLogged($BodyData) !=null){
            $productObj = Product::where([['UserID', $UserID], ['ProductID', $BodyData->id]])->first();
            $productPriceList = ProductPrice::where([ ['ProductID', $BodyData->id], ['Deleted', false]])->get();
            $productFileList = ProductFile::where([ ['ProductID', $BodyData->id], ['Deleted', false]])->get();
            return parent::responseDataSuccess(['Product' => $productObj, 'ProductPrices'=>$productPriceList, 'ProductFiles'=>$productFileList]);
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
    public function productGetList(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $listProduct = Product::where([['CreatedBy', $BodyData->$UserID],[['Deleted', false]]])->limit($BodyData->Limit)->offset($BodyData->Offset)->get();
         return parent::responseDataSuccess(['Limit'=>$BodyData->Limit, 'Offset' => $BodyData->Offset, 'ListProduct'=>$listProduct]);
      }
      return parent::responseCode(400);
   }

   /**
    * request fields
    * SessionID
    * UserID
    */
    public function productGetAll(Request $request){
      $BodyData = $request->BodyData;
      
      if(parent::isLogged($BodyData) !=null){
         $listProduct = Product::where([['CreatedBy', $BodyData->UserID], ['Deleted', false]])->get();
         $listProductPrice = ProductPrice::where([ ['CreatedBy', $BodyData->UserID], ['Deleted', false]])->get();
         $listProductFile = ProductFile::where([ ['CreatedBy', $BodyData->UserID], ['Deleted', false]])->get();
         return parent::responseDataSuccess(['ListProduct'=>$listProduct, 'ProductPrices'=>$listProductPrice, 'ProductFiles'=>$listProductFile]);
      }
      return parent::responseCode(400);
   }
   

}
