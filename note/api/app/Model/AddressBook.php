<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class AddressBook  extends Model
{

	protected $fillable = ['AddressID','UserID','ContactID','AddressContent','ProtectionLevel','Country','Province','City','AddressLine1','AddressLine2','PostalCode','CreatedBy','CreatedDate','ModifiedBy','ModifiedDate','Deleted'];
	protected $table = 'AddressBook';
	protected $keyType = 'string';
	protected $primaryKey = 'AddressID';
	public $incrementing = false;
	public $timestamps = false;

    

}