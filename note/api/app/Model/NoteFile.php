<?php

namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class NoteFile  extends Model 
{

	protected $fillable = ['NoteFileID', 'FileID', 'NoteID', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate','Deleted'];
	protected $table = 'NoteFile';

	protected $keyType = 'string';
	protected $primaryKey = 'NoteFileID';
	public $incrementing = false;
	public $timestamps = false;

}