<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Bill   extends Model
{

	protected $fillable = ['BillID', 'BillIDParent', 'UserID', 'TotalPrice', 'Description', 'Status', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'Bill';

    protected $keyType = 'string';
	protected $primaryKey = 'BillID';
	public $incrementing = false;
	public $timestamps = false;

}