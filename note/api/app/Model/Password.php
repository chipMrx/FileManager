<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Password  extends Model 
{

	protected $fillable = ['PasswordID', 'PasswordContent', 'UserID', 'IsActive', 'RemindHint', 'CreatedBy', 'CreatedDate'];
	protected $table = 'Password';

	protected $keyType = 'string';
	protected $primaryKey = 'PasswordID';
	public $incrementing = false;
	public $timestamps = false;

}