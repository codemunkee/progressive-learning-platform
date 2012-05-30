/* console scanner code, which can be launched in a goroutine
 *
 * returns via a channel a struct containing a console command (one per newline)
 */

package main

import (
	"bufio"
	"fmt"
	"io"
	"os"
	"os/signal"
	"strconv"
	"strings"
)

type Console struct {
	src io.Reader
	dst chan []string
	in  *bufio.Reader
}

var (
	sig_c         = make(chan os.Signal, 1)
	r_run    bool = false
	stop_run bool = false
)

func init() {
	signal.Notify(sig_c, os.Interrupt)
	go signal_catcher()
}

func signal_catcher() {
	for {
		<-sig_c
		if r_run {
			stop_run = true
			continue
		}
		os.Exit(0)
	}
}

func (c *Console) Init(src io.Reader) *Console {
	c.src = src
	c.in = bufio.NewReader(c.src)
	return c
}

func (c *Console) Run() {
	for {
		fmt.Print(">> ")
		line, err := c.in.ReadString('\n')
		if err != nil {
			// TODO print an error the right way, perhaps with log
			fmt.Println("error")
		}
		args := strings.Split(line, " ")
		args[len(args)-1] = strings.TrimRight(args[len(args)-1], "\n")
		log("console thread got:", args)
		if !process(args) {
			return
		}
	}
}

func process(args []string) bool {
	log("process thread got:", args)
	numArgs := len(args)
	command := args[0]

	switch command {
	case "help", "h":
		printHelp()
	case "step", "s":
		n := 1
		if numArgs != 1 {
			n, _ = strconv.Atoi(args[1])
		}
		step(n)
	case "continue", "c":
		step(-1)
	case "reset", "r":
		pc = org
	case "print", "p":
		if numArgs == 1 {
			fmt.Println("not enough arguments. Try a register or an address, or p regs for all registers")
		} else if args[1] == "regs" {
			print_regs()
		} else if strings.HasPrefix(args[1], "$") {
			print_register(strings.Trim(args[1], "$"))
		} else { // it's memory we hope
			print_memory(args[1])
		}
	case "watch", "w":
		if numArgs == 1 {
			print_watches()
		} else if strings.HasPrefix(args[1], "$") {
			watch_register(strings.Trim(args[1], "$"))
		} else { // it's a memory watch
			watch_memory(args[1])
		}
	case "quit", "q":
		return false
	case "debug":
		if numArgs == 1 {
			fmt.Println(*debug)
		} else if args[1] == "true" {
			*debug = true
		} else if args[1] == "false" {
			*debug = false
		} else {
			fmt.Println("input error:", args)
		}
	case "plpfile":
		if numArgs == 1 {
			fmt.Println(*plpfile)
		} else {
			*plpfile = args[1]
			newPLPFile(*plpfile)
		}
	case "d":
		if numArgs == 1 {
			// disassemble the whole program
			for i := 0; i < len(image); i++ {
				fmt.Println(disassemble(image[i]))
			}
		} else {
			a, ok := strToAddr(args[1])
			if ok {
				inst, ok := cpu_read(a)
				if ok {
					fmt.Println(disassemble(inst))
				}
			}
		}
	default:
		fmt.Println("input error:", args)
	}
	return true
}

func printHelp() {
	fmt.Println("	help    		- print this screen")
	fmt.Println("	debug <true,false> 	- print extra debug info")
	fmt.Println("	quit    		- quit")
	fmt.Println("	plpfile	<file>		- set active plpfile")
	fmt.Println("	step,s  		- step N instructions")
	fmt.Println("	continue,c		- run until stopped with ctrl-c")
	fmt.Println("	reset,r			- reset the PC to the original org address")
	fmt.Println("	print,p 		- print memory or registers, such as:")
	fmt.Println("		print $t0")
	fmt.Println("		print regs")
	fmt.Println("		print 0xf0200000")
	fmt.Println("	d <address>		- disassemble address or whole program if blank")
	fmt.Println("	watch,w 		- watch memory or registers and print automatically when updated")
}

func print_regs() {
	for i := 0; i < 32; i++ {
		print_register(fmt.Sprintf("%v", i))
	}
}

func print_register(r string) {
	// find the register to print
	found := false
	for i, reg := range registers {
		n, ok := strconv.Atoi(r)
		if r == reg || ((n == i) && ok == nil) {
			fmt.Printf("$%02v/$%v : %#08x\n", i, reg, rf[i])
			found = true
		}
	}
	if !found {
		fmt.Println("no register:", r)
	}
}

func strToAddr(m string) (uint32, bool) {
	var a uint64
	var err error = nil
	if strings.HasPrefix(m, "0x") {
		t := strings.TrimLeft(m, "0x")
		a, err = strconv.ParseUint(t, 16, 32)
	} else {
		// assume base 10
		a, err = strconv.ParseUint(m, 10, 32)
	}
	if err != nil {
		fmt.Println("not a valid memory address:", m)
		return 0, false
	}
	return uint32(a), true
}

func print_memory(m string) {
	a, ok := strToAddr(m)
	if !ok {
		return
	}
	v, ok := cpu_read(a)
	if ok {
		fmt.Printf("%v : %#08x\n", m, v)
	}
}
