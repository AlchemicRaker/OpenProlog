% load external pls

jip_init_modules :-
	env(debug, 'on'),
	!,
	use_module('INTERNAL://com/ugos/jiprolog/flags.pl'),
	use_module('INTERNAL://com/ugos/jiprolog/list.pl'),
	use_module('INTERNAL://com/ugos/jiprolog/sys.pl'),
	use_module('INTERNAL://com/ugos/jiprolog/xsets.pl'),
	use_module('INTERNAL://com/ugos/jiprolog/setof.pl'),
	%use_module('INTERNAL://com/ugos/jiprolog/xio.pl'),
	%use_module('INTERNAL://com/ugos/jiprolog/xdb.pl'),
	%use_module('INTERNAL://com/ugos/jiprolog/xexception.pl'),
	%use_module('INTERNAL://com/ugos/jiprolog/xreflect.pl'),
	%use_module('INTERNAL://com/ugos/jiprolog/xsystem.pl'),
	%use_module('INTERNAL://com/ugos/jiprolog/xxml.pl'),
	use_module('INTERNAL://com/ugos/jiprolog/xterm.pl').



jip_init_modules :-
	use_module('INTERNAL://com/ugos/jiprolog/flags.jip'), %write('flags.jip'), nl,
	use_module('INTERNAL://com/ugos/jiprolog/list.jip'), %write('list.jip'), nl,
	use_module('INTERNAL://com/ugos/jiprolog/sys.jip'), %write('sys.jip'), nl,
	use_module('INTERNAL://com/ugos/jiprolog/xsets.jip'), %write('xsets.jip'), nl,
	use_module('INTERNAL://com/ugos/jiprolog/setof.jip'), %write('xsets.jip'), nl,
	%use_module('INTERNAL://com/ugos/jiprolog/xio.jip'), %write('xio.jip'), nl,
	%use_module('INTERNAL://com/ugos/jiprolog/xdb.jip'), %write('xdb.jip'), nl,
	%use_module('INTERNAL://com/ugos/jiprolog/xexception.jip'), %write('xexception.jip'), nl,
	%use_module('INTERNAL://com/ugos/jiprolog/xreflect.jip'), %write('xreflect.jip'), nl,
	%use_module('INTERNAL://com/ugos/jiprolog/xsystem.jip'), %write('xsystems.jip'), nl,
	%use_module('INTERNAL://com/ugos/jiprolog/xxml.jip'), %write('xxml.jip'), nl.
	use_module('INTERNAL://com/ugos/jiprolog/xterm.jip'). %write('xterm.jip'), nl.



:-jip_init_modules.
