<?php

return [

    'driver' => env('MAIL_DRIVER', 'sendmail'),
    'host' => env('MAIL_HOST', 'smtp.googlemail.com'),
    'port' => env('MAIL_PORT', 465),
    'from' => [
        'address' => env('MAIL_FROM_ADDRESS', 'hotro.anphatcs@gmail.com'),
        'name' => env('MAIL_FROM_NAME', 'AnPhatCS'),
    ],
    'encryption' => env('MAIL_ENCRYPTION', 'ssl'),
    'username' => env('MAIL_USERNAME', 'hotro.anphatcs@gmail.com'),
    'password' => env('MAIL_PASSWORD','anhngan44'),
    /*
    |--------------------------------------------------------------------------
    | Sendmail System Path
    |--------------------------------------------------------------------------
    |
    | When using the "sendmail" driver to send e-mails, we will need to know
    | the path to where Sendmail lives on this server. A default path has
    | been provided here, which will work well on most of your systems.
    |
    */

    'sendmail' => '/usr/sbin/sendmail -bs',

    /*
    |--------------------------------------------------------------------------
    | Markdown Mail Settings
    |--------------------------------------------------------------------------
    |
    | If you are using Markdown based email rendering, you may configure your
    | theme and component paths here, allowing you to customize the design
    | of the emails. Or, you may simply stick with the Laravel defaults!
    |
    */

    'markdown' => [
        'theme' => 'default',

        'paths' => [
            resource_path('views/vendor/mail'),
        ],
    ],

    /*
    |--------------------------------------------------------------------------
    | Log Channel
    |--------------------------------------------------------------------------
    |
    | If you are using the "log" driver, you may specify the logging channel
    | if you prefer to keep mail messages separate from other log entries
    | for simpler reading. Otherwise, the default channel will be used.
    |
    */

    'log_channel' => env('MAIL_LOG_CHANNEL'),

];
