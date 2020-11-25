const { formatISO } = require('date-fns')

const formatToSQLPattern = (date) => formatISO(date, { representation: 'date' })

module.exports = {
  formatToSQLPattern
}
