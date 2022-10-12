<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class RequestException  extends Model 
{

	protected $fillable = ['RequestExceptionID', 'RequestType', 'RequestCode', 'IsActive', 'TimeStart', 'TimeEnd', 'Tag', 'UserID'];
	protected $table = 'RequestException';

	protected $keyType = 'string';
	protected $primaryKey = 'RequestExceptionID';
	public $incrementing = false;
	public $timestamps = false;

}