<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class FileApp  extends Model 
{

	protected $fillable = ['FileAppID', 'Path', 'FileExtension', 'FileType', 'FileDescription', 'FileSize', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'FileApp';

	protected $keyType = 'string';
	protected $primaryKey = 'FileAppID';
	public $incrementing = false;
	public $timestamps = false;

}