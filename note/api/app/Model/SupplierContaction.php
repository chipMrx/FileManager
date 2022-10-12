<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class SupplierContaction  extends Model 
{

	protected $fillable = ['SupplierContactionID', 'ContactID', 'SupplierID', 'Position', 'PositionStartTime', 'PositionEndTime', 'Description', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifedDate','Deleted'];
	protected $table = 'SupplierContaction';

	protected $keyType = 'string';
	protected $primaryKey = 'SupplierContactionID';
	public $incrementing = false;
	public $timestamps = false;

}