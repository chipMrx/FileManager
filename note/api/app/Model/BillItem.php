<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class BillItem   extends Model
{

	protected $fillable = ['BillItemID', 'BillID', 'ProductID', 'Price', 'Quantity', 'TotalPrice', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'BillItem';

	protected $keyType = 'string';
	protected $primaryKey = 'BillItemID';
	public $incrementing = false;
	public $timestamps = false;

}