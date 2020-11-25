const { api } = require('../services/api')

class JobsService {
  create (job) {
    return api.post('/jobs', job)
  }
}

module.exports = { JobsService }
