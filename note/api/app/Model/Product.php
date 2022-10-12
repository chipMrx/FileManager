<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Product  extends Model 
{

	protected $fillable = ['ProductID', 'ProductName', 'ProductCode', 'Unit', 'Description', 'ProductState', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifedDate','Deleted'];
	protected $table = 'Product';

	protected $keyType = 'string';
	protected $primaryKey = 'ProductID';
	public $incrementing = false;
	public $timestamps = false;

}