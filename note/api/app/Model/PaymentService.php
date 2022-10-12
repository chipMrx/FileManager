<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class PaymentService extends Model 
{

	protected $fillable = ['PaymentServiceID',	'UserID',	'Title',	'ServiceContent',	'Provider',	'ServiceType', 'ProviderPassWord', 'Fee', 'RepeatFeeType', 'CreatedBy',	'CreatedDate',	'ModifiedBy',	'ModifiedDate','Deleted'];
	protected $table = 'PaymentService';

	protected $keyType = 'string';
	protected $primaryKey = 'PaymentServiceID';
	public $incrementing = false;
	public $timestamps = false;
    
}