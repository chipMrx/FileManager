<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Contracts\Auth\Factory as Auth;
//use Illuminate\Http\Request;
use Request;

class BasicAuth
{
    
    public function handle($request, Closure $next)
    {
        if ($request->getUser() != env('API_USERNAME') 
			|| $request->getPassword() != env('API_PASSWORD')) {
            return abort(401);
        }else{
            /// update 'BodyData'
            if(Request::exists('BodyData')){
                $data = self::decrypt($request->BodyData);
                $dataObj = json_decode($data);
                $request->BodyData = $dataObj;
            }else{
                $data = $request->input('BodyData');
                if(strlen($data) > 0){
                    $dataObj = json_decode($data);
                    $request->BodyData = $dataObj;
                }
            }
            return $next($request);	
		}
    }


    /**
     * + data as string
     * encode step:
     * openssl_encrypt -> (+)iv -> base64 -> bin2hex
     * 
     * decode step:
     * hex2bin -> base64 -> (-)iv -> openssl_decrypt
     */
    private function decrypt($data)
    {
        $key = env('APP_KEY');
        $vHx = hex2bin($data);
        $vB64 = base64_decode($vHx);
        $cipher="AES-128-CBC";
        $ivlen = openssl_cipher_iv_length($cipher);
        $iv = substr($vB64, 0, $ivlen);
        $c = substr($vB64, $ivlen);
        $decrypt = openssl_decrypt($c, $cipher, $key, OPENSSL_RAW_DATA, $iv);
        
        return $decrypt;

    }

}
