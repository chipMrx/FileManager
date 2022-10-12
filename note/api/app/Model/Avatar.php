<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Avatar   extends Model
{

	protected $fillable = ['AvatarID', 'UserID', 'FileAppID', 'IsActive', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate'];
	protected $table = 'Avatar';

	
	protected $keyType = 'string';
	protected $primaryKey = 'AvatarID';
	public $incrementing = false;
	public $timestamps = false;
    

}