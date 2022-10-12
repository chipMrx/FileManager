<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use Illuminate\Http\Response;
use App\Model\Session;
use App\Model\PaymentService;
use Illuminate\Http\Request;

class PaymentServiceController extends Controller
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
    * - PaymentService (Object)
    * 
    */
   public function paymentServiceSave(Request $request){
      $BodyData = $request->BodyData;
      if(parent::isLogged($BodyData) !=null){
         try{
            $paymentObj = PaymentService::updateOrCreate( [['PaymentServiceID',$BodyData->PaymentService->PaymentServiceID], ['UserID', $BodyData->UserID] ] ,(array)$BodyData->PaymentService);
            return parent::responseCode(200);     
         } catch (\Exception $e) {
            return parent::responseCode(304); // not modified
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
    public function paymentServiceDelete(Request $request){
      $BodyData = $request->BodyData;
       if(sizeof($BodyData->IDs) > 0){
         if(parent::isLogged($BodyData) !=null){
            PaymentService::whereIn('PaymentServiceID', $BodyData->IDs)
            ->where([['UserID', $BodyData->UserID],['Deleted', 0]])
            ->update(['Deleted'=> 1, 'ModifiedDate' => $BodyData->Time]);;
            return parent::responseCode(200);
         }
      }
      return parent::responseCode(400);// bad request
   }

   /**
    * request fields
    * SessionID
    * UserID
    */
    public function paymentServiceGet(Request $request){
      $BodyData = $request->BodyData;

      if(parent::isLogged($BodyData) !=null){
         $listPayment = PaymentService::where([['UserID', $BodyData->UserID], ['Deleted', false]])->get();
         return parent::responseDataSuccess(['PaymentServices'=>$listPayment]);   
      }
      return parent::responseCode(400);
   }
   
}
