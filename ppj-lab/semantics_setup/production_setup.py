import re

prod_normalna = '([^\s]+) ::= (.+)$'
prod_nastavak = '[^\s]+ (.+)$'

bnf  = open('produkcije_bnf.txt','r')
sve  = open('produkcije_sve.txt','w')
enum = open('ProductionEnum.java','w')

xx = 0

def write_production(left, right, num):
    global xx
    production = left + ' ::= ' + right + '\n'
    prod_enum  = left[1:-1] + '_' + str(num)
    sve.write(str(xx) + ' ' + production )
    enum.write(prod_enum + ', // ' + production)
    xx += 1

enum.write('''package hr.fer.zemris.ppj.semantic;

/**
 * @author fhrenic
 */
public enum ProductionEnum{''')

num = 0
for line in bnf:
    num += 1
    y = re.findall(prod_normalna, line[:-1])
    if y:
        curr = y[0][0]
        num = 1
        enum.write('\n// ' + curr + '\n')
        write_production(curr, y[0][1], num)
    else:
        y = re.findall(prod_nastavak, line[:-1])
        write_production(curr, y[0], num)

enum.write('\n}')