<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class JobFile  extends Model 
{

	protected $fillable = ['JobFileID', 'JobID', 'FileID', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'JobFile';

	protected $keyType = 'string';
	protected $primaryKey = 'JobFileID';
	public $incrementing = false;
	public $timestamps = false;

}