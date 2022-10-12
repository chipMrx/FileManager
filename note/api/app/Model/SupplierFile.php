<?php
namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class SupplierFile extends Model
{

	protected $fillable = ['SupplierFileID', 'SupplierID', 'FileID', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'SupplierFile';

	protected $keyType = 'string';
	protected $primaryKey = 'SupplierFileID';
	public $incrementing = false;
	public $timestamps = false;

}