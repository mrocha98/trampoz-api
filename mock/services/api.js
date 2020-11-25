const axios = require('axios')

const api = axios.create({
  baseURL: 'http://localhost:3333',
  auth: {
    username: 'admin@email.com',
    password: 'admin123'
  }
})

module.exports = {
  api
}
