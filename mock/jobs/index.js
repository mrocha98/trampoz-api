const { JobsFactory } = require('./jobs.factory')
const { JobsService } = require('./jobs.service')

module.exports = {
  JobsFactory: new JobsFactory(),
  JobsService: new JobsService()
}
