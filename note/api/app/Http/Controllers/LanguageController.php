<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Support\Facades\Hash;
use App\Http\Controllers\Controller;
use Illuminate\Http\Response;
use App\Model\Language;
use App\Model\RequestException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class LanguageController extends Controller

{

    use AuthenticatesUsers;

	public function __construct(){
      $this->middleware('basicAuth');
   }





   /**
    * request fields
    * - Language (Object send as json)
    * must parse to array 
    * 
    */

    public function languageSave(Request $request){
      $BodyData = $request->BodyData;

      try{
         $languageObj = Language::updateOrCreate( [['LanguageID',$BodyData->Language->LanguageID] ] ,(array)$BodyData->Language);
         return parent::responseCode(200);     
      } catch (\Exception $e) {
         return parent::responseCode(304); // not modified
      }
   }
/**
    * request fields
    */
    public function languageGetAll(Request $request){
      $listLanguage = Language::get();
      return parent::responseDataSuccess(['Languages'=>$listLanguage]);
   }
}

