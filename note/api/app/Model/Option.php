<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Option extends Model 
{

	protected $fillable = ['OptionID', 'OptionIDParent', 'Title', 'Description', 'OptionType', 'Str1', 'Str2', 'Str3', 'Str4', 'Str5', 'Int1', 'Int2', 'Int3', 'Int4', 'Int5', 'UserID','Deleted'];
	protected $table = 'Option';

	protected $keyType = 'string';
	protected $primaryKey = 'OptionID';
	public $incrementing = false;
	public $timestamps = false;





}