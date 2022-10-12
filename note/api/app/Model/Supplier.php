<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Supplier  extends Model 
{

	protected $fillable = ['SupplierID', 'SupplierName', 'Address', 'AddressLongtitude', 'AddressLatitude', 'PhoneNumber', 'Website', 'Email', 'TaxCode', 'FoundingDate', 'Major', 'Description', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'Supplier';

	protected $keyType = 'string';
	protected $primaryKey = 'SupplierID';
	public $incrementing = false;
	public $timestamps = false;

}