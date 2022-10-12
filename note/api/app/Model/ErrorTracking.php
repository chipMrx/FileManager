<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class ErrorTracking  extends Model 
{

	protected $fillable = ['ErrorID', 'SDKVersion', 'ReleaseVersion', 'ReleaseName', 'ReleaseTime', 'Incremental', 'BuildBrand', 'BuildDevice', 'BuildID', 'Model', 'Product', 'StackTrace', 'ErrorComment', 'CreatedBy', 'CreatedDate'];
	protected $table = 'ErrorTracking';

	protected $keyType = 'string';
	protected $primaryKey = 'ErrorID';
	public $incrementing = false;
	public $timestamps = false;

}