const app = getApp()

Page({
  data: {
    name: '',
    company: '',
    phone: '',
    description: '',
    submitting: false,
    submitStatus: null
  },

  onNameInput(e) {
    this.setData({ name: e.detail.value })
  },

  onCompanyInput(e) {
    this.setData({ company: e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onDescInput(e) {
    this.setData({ description: e.detail.value })
  },

  validateForm() {
    const { name, company, phone } = this.data
    
    if (!name.trim()) {
      wx.showToast({ title: '请输入姓名', icon: 'none' })
      return false
    }
    
    if (!company.trim()) {
      wx.showToast({ title: '请输入公司名称', icon: 'none' })
      return false
    }
    
    if (!phone.trim()) {
      wx.showToast({ title: '请输入联系电话', icon: 'none' })
      return false
    }
    
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(phone)) {
      wx.showToast({ title: '请输入正确的手机号码', icon: 'none' })
      return false
    }
    
    return true
  },

  submitForm() {
    if (!this.validateForm()) return
    
    this.setData({ submitting: true, submitStatus: null })
    
    const { name, company, phone, description } = this.data
    
    wx.request({
      url: 'https://lumi-tech-server.onrender.com/api/contact',
      method: 'POST',
      header: {
        'Content-Type': 'application/json'
      },
      data: {
        name,
        company,
        phone,
        description
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 0) {
          this.setData({ submitStatus: 'success' })
          this.setData({ name: '', company: '', phone: '', description: '' })
          wx.showToast({ title: '提交成功', icon: 'success' })
        } else {
          this.setData({ submitStatus: 'error' })
          wx.showToast({ title: '提交失败', icon: 'none' })
        }
      },
      fail: (err) => {
        this.setData({ submitStatus: 'error' })
        wx.showToast({ title: '网络错误', icon: 'none' })
        console.error('Submit error:', err)
      },
      complete: () => {
        this.setData({ submitting: false })
      }
    })
  }
})