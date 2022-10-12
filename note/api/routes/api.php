<?php

    use Illuminate\Http\Request;
    use App\Http\Controllers\UserController;
    use App\Http\Controllers\NoteController;
    use App\Http\Controllers\MailController;
    use App\Http\Controllers\FileController;
    use App\Http\Controllers\CommonController;
    use App\Http\Controllers\PaymentServiceController;
    use App\Http\Controllers\LanguageController;
    use App\Http\Controllers\OptionController;
    use App\Http\Controllers\BillController;
    use App\Http\Controllers\SupplierController;
    use App\Http\Controllers\ProductController;

/*

|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
| #HTTP Exceptions
| abort(404);
| abort(403, 'Unauthorized action.');
|
|
*/

    Route::get('/', function () {
        return abort(404);
    });

/*
****************************************************
** common
****************************************************
*/

    Route::post('/getServerConfig/', 'CommonController@getServerConfig');
    Route::post('/sendErrors/', 'CommonController@sendErrors');
    Route::post('/optionGetAll/','CommonController@optionGetAll' );
    Route::post('/optionSave/','CommonController@optionSave' );
    Route::post('/optionDelete/','CommonController@optionDelete' );


    Route::post('/languageSave/', 'LanguageController@languageSave');
    Route::post('/languageGetAll/', 'LanguageController@languageGetAll');

/*
****************************************************
** User
****************************************************
*/
    Route::post('/registerSession/', 'UserController@registerSession');
    Route::post('/logOut/', 'UserController@logOut');

    Route::post('/resetPassword/', 'UserController@resetPassword');
    Route::post('/getCode/', 'UserController@getCode');

    Route::post('/updateProfile/', 'UserController@updateProfile');
    Route::post('/changeAccount/', 'UserController@changeAccount');
    Route::post('/changeMail/', 'UserController@changeMail');
    Route::post('/changePassword/', 'UserController@changePassword');


/*
****************************************************
** note
****************************************************
*/
    Route::post('/noteSave/','NoteController@noteSave' );
    Route::post('/noteDelete/','NoteController@noteDelete' );
    Route::post('/noteDeleteFile/','NoteController@noteDeleteFile' );
    Route::post('/noteGetDetail/','NoteController@noteGetDetail' );
    Route::post('/noteGetList/','NoteController@noteGetList' );
    Route::post('/noteGetAll/','NoteController@noteGetAll' );


/*
****************************************************
** file app
****************************************************
*/

    Route::post('/fileSave/','FileController@fileSave');
    Route::post('/fileGetAll/','FileController@fileGetAll');
    Route::post('/fileDelete/','FileController@fileDelete');
    Route::post('/fileGetList/','FileController@fileGetList');

/*
****************************************************
** payment service
****************************************************
*/

    Route::post('/paymentServiceGet/','PaymentServiceController@paymentServiceGet');
    Route::post('/paymentServiceSave/','PaymentServiceController@paymentServiceSave');
    Route::post('/paymentServiceDelete/','PaymentServiceController@paymentServiceDelete');

/*
****************************************************
** Bill
****************************************************
*/
    Route::post('/billSave/','BillController@billSave' );
    Route::post('/billDelete/','BillController@billDelete' );
    Route::post('/billDeleteItem/','BillController@billDeleteItem' );
    Route::post('/billDeleteFile/','BillController@billDeleteFile' );
    Route::post('/billGetDetail/','BillController@billGetDetail' );
    Route::post('/billGetAll/','BillController@billGetAll' );
    Route::post('/billGetList/','BillController@billGetList' );

/*
****************************************************
** Supplier
****************************************************
*/
    Route::post('/supplierSave/','SupplierController@supplierSave' );
    Route::post('/supplierDelete/','SupplierController@supplierDelete' );
    Route::post('/supplierDeleteContact/','SupplierController@supplierDeleteContact' );
    Route::post('/supplierDeleteFile/','SupplierController@supplierDeleteFile' );
    Route::post('/supplierGetDetail/','SupplierController@supplierGetDetail' );
    Route::post('/supplierGetList/','SupplierController@supplierGetList' );
    Route::post('/supplierGetAll/','SupplierController@supplierGetAll' );
/*
****************************************************
** Product
****************************************************
*/
    Route::post('/productSave/','ProductController@productSave' );
    Route::post('/productDelete/','ProductController@productDelete' );
    Route::post('/productDeleteFile/','ProductController@productDeleteFile' );
    Route::post('/productDeletePrice/','ProductController@productDeletePrice' );
    Route::post('/productGetDetail/','ProductController@productGetDetail' );
    Route::post('/productGetAll/','ProductController@productGetAll' );
    Route::post('/productGetList/','ProductController@productGetList' );

