<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class PhoneBook  extends Model 
{

	protected $fillable = ['PhoneID', 'serID', 'ContactID', 'PhoneContent', 'ProtectionLevel', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'PhoneBook';

	protected $keyType = 'string';
	protected $primaryKey = 'PhoneID';
	public $incrementing = false;
	public $timestamps = false;

}