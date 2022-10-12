<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Contact  extends Model 
{

	protected $fillable = ['ContactID', 'ContactName', 'UserID', 'BirthDay', 'Gender', 'Notes', 'Avatar', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'Contact';

	protected $keyType = 'string';
	protected $primaryKey = 'ContactID';
	public $incrementing = false;
	public $timestamps = false;

}