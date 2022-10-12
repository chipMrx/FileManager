<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class BillFile  extends Model 
{

	protected $fillable = ['BillFileID', 'FileID', 'BillID', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'BillFile';

	protected $keyType = 'string';
	protected $primaryKey = 'BillFileID';
	public $incrementing = false;
	public $timestamps = false;

}