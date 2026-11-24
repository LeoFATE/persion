<template>
  <div style="padding:24px; max-width:800px; margin:0 auto">
    <el-card>
      <h2 slot="header">新增员工 (t_emp)</h2>

      <el-form :model="form" ref="empForm" label-width="120px" class="demo-form">
        <el-form-item label="姓名 (e_name)">
          <el-input v-model="form.e_name" placeholder="请输入姓名"></el-input>
        </el-form-item>

        <el-form-item label="入职日期 (e_hire_date)" prop="e_hire_date" :rules="[{ required: true, message: '入职日期为必填', trigger: 'change' }]">
          <el-date-picker v-model="form.e_hire_date" type="date" placeholder="选择日期" style="width: 100%;"></el-date-picker>
        </el-form-item>

        <el-form-item label="部门编号 (e_d_id)">
          <el-input v-model="form.e_d_id" placeholder="可选，输入数字"></el-input>
        </el-form-item>

        <el-form-item label="性别 (e_sex)">
          <el-select v-model="form.e_sex" placeholder="请选择">
            <el-option label="男 (M)" value="M"></el-option>
            <el-option label="女 (F)" value="F"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">提交</el-button>
          <el-button @click="reset" style="margin-left:8px">重置</el-button>
          <router-link to="/emps"><el-button type="text" style="margin-left:8px">查看</el-button></router-link>
        </el-form-item>

        <el-alert v-if="message" :title="message" :type="success ? 'success' : 'error'" show-icon closable style="margin-top:12px"/>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'EmpForm',
  data() {
    return {
      form: { e_name: '', e_hire_date: '', e_d_id: '', e_sex: '' },
      message: '',
      success: false
    };
  },
  methods: {
    validate() {
      if (!this.form.e_hire_date) {
        this.message = '入职日期 (e_hire_date) 为必填项';
        this.success = false;
        return false;
      }
      if (this.form.e_name) this.form.e_name = this.form.e_name.trim();
      return true;
    },
    formatDateForBackend(val) {
      if (!val) return null;
      // val may be Date object or string yyyy-MM-dd
      if (typeof val === 'string') return val;
      const d = new Date(val);
      const yyyy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, '0');
      const dd = String(d.getDate()).padStart(2, '0');
      return `${yyyy}-${mm}-${dd}`;
    },
    async submit() {
      this.message = '';
      if (!this.validate()) return;
      const payload = {
        e_name: this.form.e_name,
        e_hire_date: this.formatDateForBackend(this.form.e_hire_date),
        e_d_id: this.form.e_d_id ? Number(this.form.e_d_id) : null,
        e_sex: this.form.e_sex || null
      };
      try {
        const res = await axios.post('/api/emps', payload);
        if (res.status === 201) {
          this.message = '提交成功';
          this.success = true;
          this.reset(false);
        } else {
          this.message = '服务器返回: ' + JSON.stringify(res.data);
          this.success = false;
        }
      } catch (err) {
        console.error(err);
        if (err.response && err.response.data) {
          this.message = '错误: ' + JSON.stringify(err.response.data);
        } else {
          this.message = '网络或服务器错误: ' + err.message;
        }
        this.success = false;
      }
    },
    reset(clearMessage = true) {
      this.form = { e_name: '', e_hire_date: '', e_d_id: '', e_sex: '' };
      if (clearMessage) { this.message = ''; this.success = false; }
    }
  }
};
</script>

<style scoped>
.demo-form { max-width: 700px }
.el-card { padding: 16px }
</style>
