<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Language  extends Model 
{

	protected $fillable = ['LanguageID', 'LatinName', 'NativeName', 'CodeType1', 'CodeType2t', 'CodeType2b', 'CodeType3', 'TrainedDataLink', 'LanguageFamily', 'Notes'];
	protected $table = 'ISO_Language';

	protected $keyType = 'string';
	protected $primaryKey = 'LanguageID';
	public $incrementing = false;
	public $timestamps = false;

}