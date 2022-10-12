<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class ProductPrice  extends Model 
{

	protected $fillable = ['ProductPriceID', 'ProductID', 'SupplierID', 'IsActive', 'Price', 'PriceType', 'CurrencyCode', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'ProductPrice';

	protected $keyType = 'string';
	protected $primaryKey = 'ProductPriceID';
	public $incrementing = false;
	public $timestamps = false;

}