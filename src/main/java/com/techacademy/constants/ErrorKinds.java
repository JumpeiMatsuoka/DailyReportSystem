package com.techacademy.constants;

//エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR("値を入力してください"),
    // 半角英数字チェックエラー
    HALFSIZE_ERROR("パスワードは半角英数字のみで入力してください"),
    // 桁数(8桁~16桁以外)チェックエラー
    RANGECHECK_ERROR("8文字以上16文字以下で入力してください"),
    // 重複チェックエラー(例外あり)
    DUPLICATE_EXCEPTION_ERROR("既に登録されている社員番号です"),
    // 重複チェックエラー(例外なし)
    DUPLICATE_ERROR("既に登録されている社員番号です"),
    // ログイン中削除チェックエラー
    LOGINCHECK_ERROR("ログイン中の従業員を削除することは出来ません"),
    // 日付チェックエラー
    DATECHECK_ERROR("既に登録されている日付です"),
    // 日付空白チェックエラー
    REPORT_DATE_BLANK_ERROR("値を入力してください"),
    // タイトル空白チェックエラー
    REPORT_TITLE_BLANK_ERROR("値を入力してください"),
    // タイトル桁数チェックエラー
    REPORT_TITLE_SIZE_ERROR("100文字以下で入力してください"),
    // 内容空白チェックエラー
    REPORT_CONTENT_BLANK_ERROR("値を入力してください"),
    // 内容桁数チェックエラー
    REPORT_CONTENT_SIZE_ERROR("600文字以下で入力してください"),
    // チェックOK
    CHECK_OK("チェックOK"),
    // 正常終了
    SUCCESS("正常終了"),
    NOT_FOUND_ERROR("従業員が見つかりません"),
    NAME_REQUIRED("氏名は必須です"),
    NAME_LENGTH_ERROR("氏名は20文字以下で入力してください"),
    PASSWORD_LENGTH_ERROR("パスワードは8文字以上16文字以下で入力してください"),
    PASSWORD_FORMAT_ERROR("パスワードは半角英数字のみで入力してください"),
    NOT_FOUND("見つかりません");

    // エラーメッセージ
    private final String message;

    // コンストラクタ
    private ErrorKinds(String message) {
        this.message = message;
    }

    // エラーメッセージの取得
    public String getMessage() {
        return message;
    }
}

