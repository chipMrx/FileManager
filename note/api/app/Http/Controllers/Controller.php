<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Bus\DispatchesJobs;
use Illuminate\Routing\Controller as BaseController;
use Illuminate\Foundation\Validation\ValidatesRequests;
use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use App\Model\Session;


class Controller extends BaseController
{
    const ID_LENGTH =50;
    const MEDIUM_LENGTH =100;
    const LARGE_LENGTH =200;
    const HUGE_LENGTH =1000;

    use AuthorizesRequests, DispatchesJobs, ValidatesRequests;

    public function generateID(){
        return sprintf('%04X%04X-%04X-%04X-%04X-%04X%04X%04X',
            mt_rand(0, 65535), 
            mt_rand(0, 65535), 
            mt_rand(0, 65535), 
            mt_rand(16384, 20479), 
            mt_rand(32768, 49151), 
            mt_rand(0, 65535),
            mt_rand(0, 65535), 
            mt_rand(0, 65535));
    }

    /**
     * auto gen code for special request
     */
    public function generateCode(){
        return str_pad(rand(0, 99999), 5, '0', STR_PAD_LEFT);
    }

     /**
    * when the user logs back in
    * ex: open application with cache user info and session
    * return : true / false
    * Request input:
    * - UserID
    * - SessionID
    * - DeviceID
    * - SessionType
    */
    public function isLogged($request){
        $userID = $request->UserID;
        $sessionID = $request->SessionID;
        $deviceID = $request->DeviceID;
        $sessionType = $request->SessionType;
        return self::isLoggedFields($userID, $sessionID, $deviceID, $sessionType);
    }
    public function isLoggedFields($userID, $sessionID, $deviceID, $sessionType){
        if(strlen($sessionID) >0 
         && strlen($userID) >0 ){
            $session = Session::where([['UserID',$userID], ['SessionID', $sessionID],['SessionType', $sessionType ], ['DeviceID', $deviceID], ['IsActive', true]])->first();
            return $session;
         }
         return null;
    }

    ////For more info
    /**
     * Informational responses (100–199),
     * Successful responses (200–299),
     * Redirects (300–399),
     * Client errors (400–499),
     *      400 Bad Request
     *      401 Unauthorized
     *      403 Forbidden
     *      404 Not Found
     *      405 Method Not Allowed
     * Server errors (500–599).
     */

     public function responseMessage($message, $code){
        return self::responseData(['Message'=>$message], $code);
     }

    public function responseCode($code){
        $message = "";
        switch ($code) {
            case 200:
                $message = "Success!";
            break;
            case 304:
                $message = "Not Modified!";
            break;
            case 400:
                $message = "Bad request!";
            break;
            case 401:
                $message = "Unauthorized!";
            break;
            case 403:
                $message = "Forbidden!";
            break;
            case 404:
                $message = "Not found!";
            break;
            case 405:
                $message = "Method not allowed!";
            break;
            default:
            $message = "Server error!";
        }
        return self::responseData(['Message'=>$message], $code);
    }

    public function responseData($data, $code){
        $enc = self::encrypt(json_encode($data));
        return response()->json(['BodyData'=>$enc], $code);
     }

     public function responseDataSuccess($data){
        $enc = self::encrypt(json_encode($data));
        return response()->json(['BodyData'=>$enc]);
     }

    function encrypt($data)
    {
        $key = env('APP_KEY');
        $cipher="AES-128-CBC";
        $ivlen = openssl_cipher_iv_length($cipher);
        $iv = openssl_random_pseudo_bytes($ivlen);
        $ciphertext_raw = openssl_encrypt($data, $cipher, $key, OPENSSL_RAW_DATA, $iv);
        $vB64 = base64_encode($iv.$ciphertext_raw);
        $vHx = bin2hex($vB64);
        return $vHx;
    }
   


}
