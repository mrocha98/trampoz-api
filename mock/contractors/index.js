const { ContractorsService } = require('./contractors.service')
const { ContractorsFactory } = require('./contractors.factory')

module.exports = {
  ContractorsService: new ContractorsService(),
  ContractorsFactory: new ContractorsFactory()
}
