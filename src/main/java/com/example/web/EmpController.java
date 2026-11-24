package com.example.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.entity.Emp;
import com.example.mapper.EmpMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emps")
public class EmpController {

    private final EmpMapper empMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public EmpController(EmpMapper empMapper) {
        this.empMapper = empMapper;
    }

    // 简单 DTO 用 Map 接收 JSON
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createEmp(@RequestBody Map<String, Object> body) {
        // 读取并校验必填字段 e_hire_date（e_name 可选）
        Object nameObj = body.get("e_name");
        Object hireDateObj = body.get("e_hire_date");

        if (hireDateObj == null || String.valueOf(hireDateObj).trim().isEmpty()) {
            return badRequest("e_hire_date is required");
        }

        String eName = nameObj == null ? null : String.valueOf(nameObj).trim();
        String hireDateStr = String.valueOf(hireDateObj).trim();

        // 解析 hire date，期望格式 yyyy-MM-dd
        Date hireDate;
        try {
            hireDate = Date.valueOf(java.time.LocalDate.parse(hireDateStr));
        } catch (Exception ex) {
            return badRequest("e_hire_date must be in yyyy-MM-dd format");
        }

        // 可选字段
        Integer eDId = null;
        String eSex = null;
        if (body.get("e_d_id") != null) {
            try {
                eDId = Integer.valueOf(String.valueOf(body.get("e_d_id")));
            } catch (NumberFormatException ignored) {
            }
        }
        if (body.get("e_sex") != null) {
            eSex = String.valueOf(body.get("e_sex")).trim();
        }

        Emp emp = new Emp();
        emp.setEName(eName);
        emp.setEDId(eDId);
        emp.setEHireDate(hireDate);
        emp.setESex(eSex);

        try {
            int updated = empMapper.insertEmp(emp);
            Map<String, Object> resp = new HashMap<>();
            resp.put("updated", updated);
            resp.put("e_id", emp.getEId());
            resp.put("message", "insert success");
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", "database error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    private ResponseEntity<Map<String, String>> badRequest(String msg) {
        Map<String, String> m = new HashMap<>();
        m.put("error", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(m);
    }

    // 查询员工（支持服务端分页）
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> listEmps(@RequestParam(value = "page", required = false) Integer page,
                                      @RequestParam(value = "size", required = false) Integer size) {
        try {
            int p = (page == null || page < 1) ? 1 : page;
            int s = (size == null || size < 1) ? 10 : size;
            int offset = (p - 1) * s;

            List<Emp> list = empMapper.selectPage(offset, s);
            int total = empMapper.countAll();

            Map<String, Object> resp = new HashMap<>();
            resp.put("total", total);
            resp.put("page", p);
            resp.put("size", s);
            resp.put("data", list);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", "database error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    // 更新员工
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateEmp(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body) {
        if (id == null) return badRequest("id is required");

        Object nameObj = body.get("e_name");
        Object hireDateObj = body.get("e_hire_date");

        String eName = nameObj == null ? null : String.valueOf(nameObj).trim();
        Date hireDate = null;
        if (hireDateObj != null && !String.valueOf(hireDateObj).trim().isEmpty()) {
            try {
                hireDate = Date.valueOf(java.time.LocalDate.parse(String.valueOf(hireDateObj).trim()));
            } catch (Exception ex) {
                return badRequest("e_hire_date must be in yyyy-MM-dd format");
            }
        }

        Integer eDId = null;
        String eSex = null;
        if (body.get("e_d_id") != null) {
            try { eDId = Integer.valueOf(String.valueOf(body.get("e_d_id"))); } catch (NumberFormatException ignored) {}
        }
        if (body.get("e_sex") != null) {
            eSex = String.valueOf(body.get("e_sex")).trim();
        }

        Emp emp = new Emp();
        emp.setEId(id);
        emp.setEName(eName);
        emp.setEDId(eDId);
        emp.setEHireDate(hireDate);
        emp.setESex(eSex);

        try {
            int updated = empMapper.updateEmp(emp);
            Map<String, Object> resp = new HashMap<>();
            resp.put("updated", updated);
            resp.put("message", "update success");
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", "database error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    // 查询部门映射 d_id -> d_name
    @GetMapping(value = "/depts", produces = "application/json")
    public ResponseEntity<?> listDepts() {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT d_id, d_name FROM ems.t_dept");
            Map<Integer, String> map = new HashMap<>();
            for (Map<String, Object> r : rows) {
                Object idObj = r.get("d_id");
                Object nameObj = r.get("d_name");
                if (idObj != null) {
                    Integer id = ((Number) idObj).intValue();
                    map.put(id, nameObj == null ? null : String.valueOf(nameObj));
                }
            }
            return ResponseEntity.ok(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", "database error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}

