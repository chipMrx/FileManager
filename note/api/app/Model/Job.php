<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Job  extends Model 
{

	protected $fillable = ['JobID', 'serID', 'ParentJobID', 'JobTitle', 'JobDescription', 'Status', 'ProtectionLevel', 'StartDate', 'DueDate', 'Percent', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'Job';

	protected $keyType = 'string';
	protected $primaryKey = 'JobID';
	public $incrementing = false;
	public $timestamps = false;

}