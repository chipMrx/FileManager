<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class WebsiteBook extends Model 
{

	protected $fillable = ['WebsiteID',	'UserID',	'ContactID',	'WebsiteContent',	'ProtectionLevel',	'CreatedBy',	'CreatedDate',	'ModifiedBy',	'ModifiedDate','Deleted'];
	protected $table = 'WebsiteBook';

	protected $keyType = 'string';
	protected $primaryKey = 'WebsiteID';
	public $incrementing = false;
	public $timestamps = false;
    
}