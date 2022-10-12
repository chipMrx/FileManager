<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class UserApp  extends Model 
{

	protected $fillable = ['UserID', 'UserName', 'EmailAddress', 'CreatedBy', 'CreatedDate', 'Deleted'];
	protected $table = 'UserApp';

	protected $keyType = 'string';
	protected $primaryKey = 'UserID';
	public $incrementing = false;
	public $timestamps = false;

}