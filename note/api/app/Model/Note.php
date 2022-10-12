<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Note  extends Model 
{

	protected $fillable = ['NoteID', 'UserID', 'NoteContent', 'NoteTitle', 'ProtectionLevel', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'Note';

	protected $keyType = 'string';
	protected $primaryKey = 'NoteID';
	public $incrementing = false;
	public $timestamps = false;

}