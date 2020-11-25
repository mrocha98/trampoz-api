const { get, map } = require('lodash')
const { api } = require('../services/api')

class ContractorsService {
  async getIds () {
    const { data } = await api.get('/contractors')

    const contractors = get(data, 'contractors', [])
    const ids = map(contractors, (contractor) => contractor.id)

    return ids
  }

  async create (contractor) {
    return api.post('/contractors', contractor)
  }
}

module.exports = { ContractorsService }
