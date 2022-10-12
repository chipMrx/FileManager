<?php
namespace App\Http\Controllers;
 
use App\Http\Controllers\Controller;
use App\Mail\CodeEmail;
use Illuminate\Support\Facades\Mail;
use Illuminate\Http\Request;
 
class MailController extends Controller
{

	public function sendMail($requestType, $mail, $code){
		switch ($requestType) {
			case "rq_rs_pw":
				self::sendResetPassCode($mail, $code);
			break;
			case "rq_ch_acc":
				self::sendChangeAccountCode($mail, $code);
			break;
			case "rq_ch_mail":
				self::sendChangeEmailCode($mail, $code);
			break;
		}
	}
	/**
	* Email
	* code
	*/
	public function sendResetPassCode( $mail, $code)
    {
		$request = (object) array(
			'code' => $code,
			'title' => 'Forgot password!',
			'subject'=>'Support reset password!',
			'request'=>'password reset');
        Mail::to($mail)->send(new CodeEmail($request));
	}

	/**
	* Email
	* code
	*/
	public function sendChangeAccountCode( $mail, $code)
    {
		$request = (object) array(
			'code' => $code,
			'title' => 'Change account name!',
			'subject'=>'Support change account name!',
			'request'=>'change account');
        Mail::to($mail)->send(new CodeEmail($request));
	}

	/**
	* Email
	* code
	*/
	public function sendChangeEmailCode( $mail, $code)
    {
		$request = (object) array(
			'code' => $code,
			'title' => 'Change email!',
			'subject'=>'Support change email!',
			'request'=>'change email');
        Mail::to($mail)->send(new CodeEmail($request));
	}
}