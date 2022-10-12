<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class ProductFile  extends Model 
{

	protected $fillable = ['ProductFileID', 'ProductID', 'FileID', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'ProductFile';

	protected $keyType = 'string';
	protected $primaryKey = 'ProductFileID';
	public $incrementing = false;
	public $timestamps = false;

}