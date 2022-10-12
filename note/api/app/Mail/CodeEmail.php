<?php
 
namespace App\Mail;
 
use Illuminate\Bus\Queueable;
use Illuminate\Mail\Mailable;
use Illuminate\Queue\SerializesModels;
use Illuminate\Contracts\Queue\ShouldQueue;
 
class CodeEmail extends Mailable
{
    use Queueable, SerializesModels;
     
    /**
     * The data object instance.
     * : title
     * : subject
     * : logo
     * : code
     * : appName
     * : companyAddress
     */
    public $data;
 
    public function __construct($data)
    {
        $data->logo = 'http://api.anphatcs.com/public/images/logo_emma.png';
        $data->appName = 'EMMA';
        $data->companyAddress = '161D/106/44M Lac Long Quan, ward 3, district 11, HCM cty.';
        $this->data = $data;
    }
 
    /**
     * Build the message.
     *
     * @return $this
     */
    public function build()
    {
        return $this->from('hotro.anphatcs@gmail.com', $this->data->title)
                    ->subject($this->data->subject)
                    ->view('mails.code')
                    ->text('mails.code_plain');
    }
}