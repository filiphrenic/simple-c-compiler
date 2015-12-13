import re

prod_normalna = '([^\s]+) ::= (.+)$'
prod_nastavak = '[^\s]+ (.+)$'

bnf  = open('produkcije_bnf.txt','r')
sve  = open('produkcije_sve.txt','w')
enum = open('ProductionEnum.java','w')
analyzer = open('SemanticAnalyzer.java','w')

xx = 0

def case(pe,production):
    return 'else if (pe==ProductionEnum.' + pe + '){\n// ' + production + '}\n'

def write_production(left, right, num):
    global xx
    production = left + ' ::= ' + right + '\n'
    prod_enum  = left[1:-1] + '_' + str(num)
    sve.write(str(xx) + ' ' + production )
    enum.write(prod_enum + ', // ' + production)
    analyzer.write(case(prod_enum,production))
    xx += 1

header = '''package hr.fer.zemris.ppj.semantic;

/**
 * @author fhrenic
 */'''

enum.write(header + '\npublic enum ProductionEnum{')

upper = '''
public class SemanticAnalyzer{
    
    public void check(SemNodeV node){

        ProductionEnum pe = determine(node);

'''

lower = '''

    }

}
'''

analyzer.write(header + upper)



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
analyzer.write(lower)