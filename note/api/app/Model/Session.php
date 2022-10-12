<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Session  extends Model 
{

	protected $fillable = ['SessionID', 'UserID', 'SessionType', 'BuildType', 'DeviceID', 'StartTime', 'EndTime', 'IsActive', 'IpAddress'];
	protected $table = 'Session';

	protected $keyType = 'string';
	protected $primaryKey = 'SessionID';
	public $incrementing = false;
	public $timestamps = false;

}