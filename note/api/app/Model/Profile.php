<?php
namespace App\Model;

use Illuminate\Database\Eloquent\Model;

class Profile  extends Model 
{

	protected $fillable = ['ProfileID', 'UserID', 'AvatarLink' ,'UserNickName', 'BirthDay', 'Gender', 'ProtectionLevel', 'Signature', 'CreatedBy', 'CreatedDate', 'ModifiedBy', 'ModifiedDate', 'ShortIntroduce'];
	protected $table = 'Profile';

	protected $keyType = 'string';
	protected $primaryKey = 'ProfileID';
	public $incrementing = false;
	public $timestamps = false;

}