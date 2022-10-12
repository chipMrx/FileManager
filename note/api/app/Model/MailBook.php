<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class MailBook  extends Model 
{

	protected $fillable = ['MailID', 'UserID', 'ContactID', 'MailContent', 'ProtectionLevel', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'MailBook';

	protected $keyType = 'string';
	protected $primaryKey = 'MailID';
	public $incrementing = false;
	public $timestamps = false;

}