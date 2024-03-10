package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 従業員保存
    @Transactional
    public ErrorKinds save(Employee employee) {

     // パスワードチェック
        ErrorKinds result = employeePasswordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        // 従業員番号重複チェック
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        employee.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    @Transactional
    public ErrorKinds update(Employee employee) {
        // 氏名の必須チェック
        if (employee.getName() == null || employee.getName().isEmpty()) {
            return ErrorKinds.NAME_REQUIRED;
        }
        // 氏名の桁数チェック
        if (employee.getName().length() > 20) {
            return ErrorKinds.NAME_LENGTH_ERROR;
        }
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employee.getCode());
        if (!existingEmployeeOpt.isPresent()) {
            return ErrorKinds.NOT_FOUND_ERROR; // 従業員が見つからなければエラーを返す
        }

        Employee existingEmployee = existingEmployeeOpt.get();

     // パスワードが空でない場合のみチェックして更新
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            ErrorKinds passwordResult = employeePasswordCheck(employee);
            if (passwordResult != ErrorKinds.CHECK_OK) {
                return passwordResult;
            }
            existingEmployee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }
        // ここでパスワードが空の場合でも、その他の情報は更新される
        existingEmployee.setName(employee.getName());
        existingEmployee.setRole(employee.getRole());
        existingEmployee.setUpdatedAt(LocalDateTime.now());

        employeeRepository.save(existingEmployee); // 更新処理を実行
        return ErrorKinds.SUCCESS; // 更新に成功したらSUCCESSを返す
    }

    // 従業員削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {

        // 自分を削除しようとした場合はエラーメッセージを表示
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
        Employee employee = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 従業員一覧表示処理
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // 1件を検索
    public Employee findByCode(String code) {
        // findByIdで検索
        Optional<Employee> option = employeeRepository.findById(code);
        // 取得できなかった場合はnullを返す
        Employee employee = option.orElse(null);
        return employee;
    }

    // 従業員パスワードチェック
    private ErrorKinds employeePasswordCheck(Employee employee) {

        // 従業員パスワードの半角英数字チェック処理
        if (isHalfSizeCheckError(employee)) {

            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 従業員パスワードの8文字～16文字チェック処理
        if (isOutOfRangePassword(employee)) {

            return ErrorKinds.RANGECHECK_ERROR;
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        return ErrorKinds.CHECK_OK;
    }

    // 従業員パスワードの半角英数字チェック処理
    private boolean isHalfSizeCheckError(Employee employee) {

        // 半角英数字チェック
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(employee.getPassword());
        return !matcher.matches();
    }

    // 従業員パスワードの8文字～16文字チェック処理
    public boolean isOutOfRangePassword(Employee employee) {

        // 桁数チェック
        int passwordLength = employee.getPassword().length();
        return passwordLength < 8 || 16 < passwordLength;
    }

}